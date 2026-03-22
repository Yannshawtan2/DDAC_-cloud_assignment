package com.example.DDac_group18.services;

import org.springframework.stereotype.Service;

@Service
public class DataValidation {

    public boolean validateHealthData(Double weight, Double height, Double waistCircumference) {
        // Weight validation (assuming weight between 10kg and 500kg)
        if (weight == null || weight < 10 || weight > 500) {
            return false;
        }

        // Height validation (assuming height between 40cm and 300cm)
        if (height == null || height < 40 || height > 300) {
            return false;
        }

        // Waist circumference validation (assuming between 20cm and 300cm)
        if (waistCircumference == null || waistCircumference < 20 || waistCircumference > 300) {
            return false;
        }

        return true;
    }

    public boolean validateBloodPressure(String bloodPressure) {
        // Blood pressure validation (format: "systolic/diastolic")
        // Only validate if value is provided (optional field)
        if (bloodPressure == null || bloodPressure.trim().isEmpty()) {
            return true;
        }

        try {
            String[] parts = bloodPressure.split("/");
            if (parts.length != 2) {
                return false;
            }

            int systolic = Integer.parseInt(parts[0].trim());
            int diastolic = Integer.parseInt(parts[1].trim());

            // Systolic: typically 90-180 mmHg
            // Diastolic: typically 60-120 mmHg
            return systolic >= 90 && systolic <= 180 &&
                    diastolic >= 60 && diastolic <= 120 &&
                    systolic > diastolic;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateBloodGlucose(Double bloodGlucose) {
        // Blood glucose validation (assuming between 2.0 and 20.0 mmol/L)
        // Only validate if value is provided (optional field)
        return bloodGlucose == null || (bloodGlucose >= 2.0 && bloodGlucose <= 20.0);
    }
}