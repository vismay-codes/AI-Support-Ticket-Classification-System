from pathlib import Path
import json
import os
import joblib
import pandas as pd
from flask import Flask, jsonify, request
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline

BASE_DIR = Path(__file__).resolve().parent
DATA_FILE = BASE_DIR / "data" / "tickets.csv"
MODEL_DIR = BASE_DIR / "model"
MODEL_FILE = MODEL_DIR / "ticket_classifier.joblib"
METRICS_FILE = MODEL_DIR / "metrics.json"

HOST = os.getenv("ML_HOST", "127.0.0.1")
PORT = int(os.getenv("ML_PORT", "5000"))

app = Flask(__name__)
model = None
metrics = {}


def validate_dataset(df: pd.DataFrame) -> None:
    required = {"ticket_text", "category"}
    if not required.issubset(df.columns):
        raise ValueError("Dataset must contain ticket_text and category columns")
    if df["ticket_text"].isna().any() or df["category"].isna().any():
        raise ValueError("Dataset contains empty ticket text or category values")
    if df["category"].nunique() < 2:
        raise ValueError("Dataset must contain at least two categories")


def train_model() -> None:
    global model, metrics
    df = pd.read_csv(DATA_FILE)
    validate_dataset(df)
    df["ticket_text"] = df["ticket_text"].astype(str).str.strip()
    df["category"] = df["category"].astype(str).str.strip()

    X_train, X_test, y_train, y_test = train_test_split(
        df["ticket_text"],
        df["category"],
        test_size=0.20,
        random_state=42,
        stratify=df["category"],
    )

    pipeline = Pipeline([
        ("tfidf", TfidfVectorizer(lowercase=True, stop_words="english", ngram_range=(1, 2), sublinear_tf=True)),
        ("classifier", LogisticRegression(max_iter=2000, random_state=42)),
    ])

    pipeline.fit(X_train, y_train)
    predictions = pipeline.predict(X_test)
    accuracy = float(accuracy_score(y_test, predictions))
    report = classification_report(y_test, predictions, output_dict=True, zero_division=0)

    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    joblib.dump(pipeline, MODEL_FILE)
    metrics = {
        "algorithm": "TF-IDF + Logistic Regression",
        "categories": sorted(df["category"].unique().tolist()),
        "dataset_rows": int(len(df)),
        "training_rows": int(len(X_train)),
        "test_rows": int(len(X_test)),
        "validation_accuracy": round(accuracy, 4),
        "classification_report": report,
    }
    METRICS_FILE.write_text(json.dumps(metrics, indent=2), encoding="utf-8")
    model = pipeline


def load_or_train() -> None:
    global model, metrics
    # Always train on startup so the metrics and model are guaranteed to match the current CSV.
    train_model()


def predict_ticket(ticket: str) -> dict:
    probabilities = model.predict_proba([ticket])[0]
    classes = model.classes_
    best_index = int(probabilities.argmax())
    return {
        "ticket": ticket,
        "category": str(classes[best_index]),
        "confidence": round(float(probabilities[best_index]), 4),
    }


@app.get("/health")
def health():
    return jsonify({"status": "UP", "model_loaded": model is not None})


@app.get("/model-info")
def model_info():
    return jsonify(metrics)


@app.post("/predict")
def predict():
    if not request.is_json:
        return jsonify({"error": "Request must use application/json"}), 415
    payload = request.get_json(silent=True) or {}
    ticket = str(payload.get("ticket", "")).strip()
    if not ticket:
        return jsonify({"error": "ticket is required"}), 400
    return jsonify(predict_ticket(ticket))


if __name__ == "__main__":
    load_or_train()
    app.run(host=HOST, port=PORT, debug=False)
