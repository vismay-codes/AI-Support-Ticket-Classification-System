# Interview Q&A — AI Support Ticket Classification System

## 1. What problem does the project solve?
It automatically classifies customer support tickets into four predefined categories: Login Issue, Payment Issue, Technical Issue and Account Issue.

## 2. Why did you use Python?
Python has a mature machine-learning ecosystem. I used pandas and scikit-learn for data handling, TF-IDF feature extraction, model training and evaluation.

## 3. What is NLP?
Natural Language Processing is the processing of human language so that software can extract useful information from text.

## 4. Why TF-IDF?
TF-IDF converts text into numerical features. It gives higher importance to terms that are useful for distinguishing documents while reducing the influence of very common words.

## 5. Why Logistic Regression?
It is a simple, fast and interpretable classification algorithm that works well as a baseline for text classification. It also supports multi-class classification.

## 6. Is Logistic Regression really machine learning?
Yes. It is a supervised learning classification algorithm. The model learns weights from labelled training examples and uses them to predict the class of unseen text.

## 7. What is the role of Spring Boot?
Spring Boot provides the application backend and REST API. It receives the user's ticket, calls the Python ML service and stores the result in the database.

## 8. Why separate Python and Java services?
The separation keeps ML responsibilities in Python, where the scikit-learn ecosystem is convenient, while Java/Spring Boot handles the application API and database layer.

## 9. What happens when a ticket is submitted?
The browser sends the ticket to Spring Boot. Spring Boot sends the text to the Python `/predict` endpoint. Python transforms the text using the trained TF-IDF pipeline, predicts a category and confidence, and returns JSON. Spring Boot stores the result and returns it to the browser.

## 10. What is a REST API?
A REST API exposes application functionality through HTTP endpoints. In this project, POST is used for classification and GET is used for retrieving saved tickets.

## 11. What is overfitting?
Overfitting occurs when a model learns the training data too closely and does not generalize well to unseen data. A separate validation/test set helps measure generalization.

## 12. What is train-test split?
It separates the available labelled data into training data used to fit the model and test data used to evaluate it on unseen examples.

## 13. Why use stratification?
The dataset contains multiple categories. Stratification keeps their class proportions reasonably consistent between the training and test sets.

## 14. What is confidence in this project?
It is the probability-like score returned by Logistic Regression's `predict_proba` for the selected class. It is not a guarantee that the prediction is correct.

## 15. Why SQL/Oracle?
The database provides persistent storage for tickets and predictions. Oracle is also supported because it matches the SQL/Oracle skills represented on my resume.

## 16. Why is H2 the default?
H2 makes the project easy to demonstrate without depending on an Oracle server. The application also includes an Oracle profile for an Oracle environment.

## 17. What would you improve in production?
I would use a larger labelled dataset, cross-validation, model comparison, better text cleaning, authentication, monitoring, structured logging and automated model/version management.

## 18. What was the biggest challenge?
The main challenge was converting unstructured ticket text into useful numerical features while keeping the model simple enough to evaluate and explain.
