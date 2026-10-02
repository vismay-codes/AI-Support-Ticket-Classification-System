# AI-Based Support Ticket Classification System

A complete, interview-friendly AI/ML project built around the user's existing Java + Python + SQL/Oracle skill set.

## What it does

A user enters a support ticket. The Spring Boot application sends the ticket to a Python NLP service. The Python service uses TF-IDF and Logistic Regression to classify the ticket into one of four categories. Spring Boot stores the ticket, prediction and confidence in a local SQL database.

Categories:
- Login Issue
- Payment Issue
- Technical Issue
- Account Issue

## Architecture

Browser UI -> Spring Boot REST API -> Python ML REST API -> TF-IDF + Logistic Regression
                                      |
                                      v
                               SQL persistence

Default database: H2 file database (zero Oracle setup required for the demo).
Optional database profile: Oracle Database.

## Why H2 is the default

The default profile is intentionally zero-configuration so the project can run on a fresh Windows machine without requiring an Oracle listener, service, username or password. The same application also includes an Oracle JDBC dependency and an Oracle schema for the Oracle profile.

## Requirements

- Windows 10/11
- Python 3.11+ (Python 3.12 is recommended and is suitable for this project)
- Java 17+ (Java 17 or 21 is recommended)
- Internet access for the first dependency installation/build

Maven does not need to be pre-installed if `scripts/START_PROJECT.bat` is used; the script can download Maven 3.9.11 into the project tools directory when required.

## Easiest way to run on Windows

1. Extract the ZIP to a normal folder such as `D:\Projects\AI-Support-Ticket-Classifier`.
2. Double-click `scripts\START_PROJECT.bat`.
3. The script creates the Python virtual environment, installs the pinned Python packages, starts the ML service, builds/starts Spring Boot and opens the application page.
4. Open `http://localhost:8080` if the browser does not open automatically.

No source-code edits are required for the default demo.

## Manual run

### Python service

From `python-ml-service`:

```text
python -m venv .venv
.venv\\Scripts\\activate
python -m pip install -r requirements.txt
python app.py
```

ML health:
`http://127.0.0.1:5000/health`

ML model information:
`http://127.0.0.1:5000/model-info`

### Spring Boot

From `spring-api`:

```text
mvn spring-boot:run
```

Application:
`http://localhost:8080`

The browser UI is already included, so Postman is not required for a basic demo.

## API examples

### Classify a ticket

POST `/api/tickets/classify`

```json
{
  "ticket": "My payment was deducted but my order was not confirmed"
}
```

Example response shape:

```json
{
  "ticketId": "generated-id",
  "ticket": "My payment was deducted but my order was not confirmed",
  "category": "Payment Issue",
  "confidence": 0.61,
  "createdAt": "2026-09-30T12:00:00"
}
```

### Retrieve saved tickets

GET `/api/tickets`

### Retrieve total count

GET `/api/tickets/stats`

## Oracle profile

The default demo does not require Oracle. If you want to demonstrate the Oracle version after your Oracle environment is available:

1. Create an Oracle schema/user.
2. Run `oracle/schema.sql` once in that schema.
3. Set these Windows environment variables:

```text
ORACLE_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1
ORACLE_USERNAME=your_username
ORACLE_PASSWORD=your_password
```

4. Start Spring Boot with:

```text
mvn spring-boot:run -Dspring-boot.run.profiles=oracle
```

The Java repository uses standard SQL INSERT/SELECT statements and works with both the H2 default profile and the Oracle profile.

## Model details

- Text preprocessing: lowercasing + English stop-word removal
- Feature extraction: TF-IDF with unigrams and bigrams
- Classifier: Logistic Regression
- Evaluation: stratified hold-out validation
- Dataset: `python-ml-service/data/tickets.csv`

The Python service trains on startup so the saved model and reported validation metrics always correspond to the current CSV dataset.

Do not put a model accuracy number on a resume unless you have actually run the final dataset and verified the metric.

## Interview explanation

"I built a support-ticket classification system using Python NLP and a Logistic Regression model. I used TF-IDF to convert ticket text into numerical features. Spring Boot exposes the application REST API and calls the Python prediction service. The prediction and ticket details are stored in SQL, with Oracle supported as a database profile."

## Project scope

The project is intentionally limited to four categories and a classical ML pipeline. It demonstrates practical NLP, REST APIs, Java backend development and SQL persistence without introducing unnecessary LLM/RAG/vector-database complexity that would make the project difficult to defend in an interview.
