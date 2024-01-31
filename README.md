# Panacea: Innovative Mobile Health Monitoring and Lifestyle Enhancement

## Overview

Panacea is an innovative mobile application developed by the Computer Science Department at Arizona State University. This application aims to enhance user well-being by integrating location services, respiration rate, and heart rate monitoring into a single, sophisticated experience. The standout feature of Panacea is its continuous monitoring of vital health metrics, with the ability to initiate an emergency response if irregularities are detected.

## Introduction

The project focuses on developing a comprehensive mobile health monitoring application that serves as a centralized hub for users. It integrates with wearable devices to collect vital health data such as heart rate, respiratory rate, SpO2 levels, and sleep patterns. The application goes beyond data monitoring by generating a personalized lifestyle score, acting as a benchmark for users to assess their overall health.

Notably, the application addresses the lack of actionable health-based recommendations in existing apps. It provides tailored suggestions for activities and dietary choices based on each user's health profile, aligning with their specific needs and preferences. Additionally, the app excels in emergency detection and alerting, utilizing collected health data to identify potential emergencies and ensuring rapid notification of emergency services.

The project's methodology is comprehensive, involving the integration of technologies like outlier detection modules, a fuzzy logic controller, and APIs from Google Maps and Places. These components collectively contribute to the application's efficiency in data collection, emergency detection, lifestyle score computation, and offering personalized recommendations for events and restaurants.

## Features

### 1. Health Monitoring

Panacea integrates with wearable devices to collect real-time health data, including heart rate, respiratory rate, SpO2 levels, and sleep patterns. The application provides a comprehensive view of the user's health over time, allowing for close monitoring and informed decision-making.

### 2. Lifestyle Score

The application generates a personalized Lifestyle Score for each user, considering individual health metrics, lifestyle choices, and preferences. This score serves as a self-reflection tool, guiding users to understand the impact of their daily habits on overall health.

### 3. Emergency Detection and Alerting

Panacea employs advanced personalized emergency detection using the DBSCAN algorithm. In critical situations, the application automatically alerts emergency services, ensuring a rapid response to potential health emergencies.

### 4. Events and Restaurants Recommendations

Panacea leverages Google's Maps and Places APIs to offer tailored recommendations for events and dining. Based on the user's location, health profile, and preferences, the application suggests activities and restaurants that align with their health objectives.

## Architecture

Panacea's architecture involves the integration of various technologies, including outlier detection modules, a fuzzy logic controller, and APIs from Google Maps and Places. The system ensures efficient data collection, emergency detection, lifestyle score computation, and personalized recommendations. The integrated system involves continuous tracking of health metrics by a wearable device. Real-time processing of these metrics occurs on the user's smartphone app to detect abnormal readings indicating potential health risks. In case of outliers, the app can trigger alerts, including the possibility of notifying emergency services. The health data undergoes sophisticated interpretation through a fuzzy logic controller and is stored in an SQLite database for tracking and analysis.

Simultaneously, the system utilizes the user's current location data obtained from the Google Maps API. This information feeds into a recommendation engine, leveraging the Google Places API. The recommendation engine cross-references the user's health and lifestyle data, suggesting nearby activities and restaurants aligned with their health objectives. These recommendations are conveniently provided to the user through the smartphone application, which serves as a platform for both health monitoring and lifestyle management. This integration ensures that the user's health data directly influences their daily activity choices.

![Architecture](https://github.com/SaiVikhyath/Panacea/blob/main/Architecture.png)

## Implementation

Panacea's implementation is grounded in state-of-the-art technologies:

1. **Wearable Device Integration:** Wearables are integrated to collect real-time health data, including heart rate, respiratory rate, SpO2, and sleep metrics (We are using Health Connect for getting all the required data). This information is then sent to the Outlier Detection Module and stored in the SQLite database.

2. **Outlier Detection for Emergency Services:** The DBSCAN algorithm is employed for Outlier Detection, utilizing historical health data to fine-tune the algorithm’s hyperparameters. This enables personalized insights which are critical for detecting emergencies. In the event of an emergency, the application triggers an SMS using the SmsManager Class and initiates a call to paramedics using an Intent.

3. **Lifestyle Score Calculation:** A Fuzzy Logic Controller is used to compute the Lifestyle Score. It fetches historical health data from the SQLite database, applying membership functions to each health metric to calculate a personalized score between 0-100. This score is then stored in the SQLite database for ongoing tracking.

4. **Personalized Events and Restaurant Recommendations:** To offer tailored recommendations, the application leverages Google’s Maps API to determine the user’s current location and uses the Places API to fetch nearby events and restaurants. The results are then filtered based on the user’s Lifestyle Score to provide personalized recommendations that are displayed to the user.


## Conclusion

Panacea represents a groundbreaking approach to personal health monitoring and wellness management. By offering actionable insights, recommendations, and emergency support, it empowers users to make informed decisions about their health and well-being, potentially improving their quality of life significantly.


## Results
![Home_Screen](https://github.com/Vikhy18/Panacea/blob/main/HomeScreen.jpeg)
![Current_Location](https://github.com/Vikhy18/Panacea/blob/main/CurrentLocation.jpeg)
![Restaurant_Suggestion](https://github.com/Vikhy18/Panacea/blob/main/RestaurantRetrieval.jpeg)
![Events_Suggestion](https://github.com/Vikhy18/Panacea/blob/main/EventRetrieval.jpeg)


## References

1. Rahman, M. S., et al. "Wearable Health Technology and the Internet of Things: A Review." IEEE International Conference on Computer and Information Technology (CIT), 2015, pp. 1-6.

2. Smith, J. "The Impact of Wearable Technology on Medicine." IEEE Journal of Biomedical and Health Informatics, vol. 19, no. 3, 2015, pp. 1193-1200.

3. Nugent, C., et al. "Smart Health: A Context-Aware Health Paradigm within Smart Cities." Proceedings of the IEEE International Conference on Smart Cities (ICSC), 2016, pp. 1-6.
