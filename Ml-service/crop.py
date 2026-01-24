from flask import Flask, request, jsonify
import numpy as np
import joblib
import os

app = Flask(__name__)

# Base directory of this file
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

MODEL_DIR = os.path.join(BASE_DIR, "ML-Model")

# Load trained objects
model = joblib.load(os.path.join(MODEL_DIR, "crop_gnb_model.pkl"))
label_encoder = joblib.load(os.path.join(MODEL_DIR, "label_encoder.pkl"))
scaler = joblib.load(os.path.join(MODEL_DIR, "scaler.pkl"))

FEATURES = ["N", "P", "K", "temperature", "humidity", "ph", "rainfall"]


@app.route("/")
def home():
    return jsonify({
        "app": "Crop Recommendation System",
        "model": "Gaussian Naive Bayes",
        "accuracy": "99.45%",
        "status": "Running"
    })


@app.route("/predict", methods=["POST"])
def predict():
    try:
        data = request.get_json()

        # Check missing fields
        for feature in FEATURES:
            if feature not in data:
                return jsonify({
                    "error": f"Missing field: {feature}"
                }), 400

        # Extract values
        values = [
            data["N"], data["P"], data["K"],
            data["temperature"], data["humidity"],
            data["ph"], data["rainfall"]
        ]

        # Convert to numpy
        input_data = np.array(values).reshape(1, -1)

        # Scale
        input_scaled = scaler.transform(input_data)

        # Predict
        prediction = model.predict(input_scaled)[0]
        probabilities = model.predict_proba(input_scaled)[0]

        crop_name = label_encoder.inverse_transform([prediction])[0]

        # Get top 3 crops
        top_indices = np.argsort(probabilities)[::-1][:3]
        top_crops = label_encoder.inverse_transform(top_indices)

        confidence = round(probabilities[prediction] * 100, 2)

        return jsonify({
            "recommended_crop": crop_name,
            "confidence_percent": confidence,
            "top_3_crops": [
                {"crop": top_crops[0], "probability": round(probabilities[top_indices[0]]*100,2)},
                {"crop": top_crops[1], "probability": round(probabilities[top_indices[1]]*100,2)},
                {"crop": top_crops[2], "probability": round(probabilities[top_indices[2]]*100,2)}
            ],
            "input_received": dict(zip(FEATURES, values))
        })

    except Exception as e:
        return jsonify({
            "error": str(e)
        }), 500


if __name__ == "__main__":
    app.run(debug=True)
