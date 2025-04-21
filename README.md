Spring Batch Hello World – Sensor Data Processing
This project is a simple introductory Spring Batch application, inspired by the Spring Batch Mystery course on Udemy.

Overview
The application demonstrates a basic Spring Batch flow:

Reading sensor data from text files

Processing the data through defined processors

Writing the processed data to output files

Essentially, it acts as a "Hello World" for Spring Batch by showcasing how to set up a job with reader, processor, and writer components.

Getting Started
After cloning the repository, you can run the application with the following command:


java -jar target/firstpsinrgbatch-0.0.1-SNAPSHOT.jar com.smiloutcha.firstpsinrgbatch.TemperatureSensorRootConfiguration temperatureSensorJob id=$(uuidgen)
