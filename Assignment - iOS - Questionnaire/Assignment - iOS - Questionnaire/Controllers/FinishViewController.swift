//
//  FinishViewController.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 05/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit
import CoreData
import CoreLocation

/// UIViewController for the Finish Screen on `Main.storyboard`
class FinishViewController: UIViewController {
    var locationManager = CLLocationManager()
    var latitude: Double = 0
    var longitude: Double = 0

    var userId: String?
    var dateOfBirth: Date?
    var results: [Result]?

    // MARK: - Actions

    /// Handle finishing of questionnaire
    @IBAction func finish(_ sender: Any) {
        saveResponse()
        performSegue(withIdentifier: "finishSegue", sender: self)
    }

    // MARK: - ViewController Functions

    /// Save questionnaire response to CoreData
    func saveResponse() {
        guard let appDelegate = UIApplication.shared.delegate as? AppDelegate else { return }
        let managedContext = appDelegate.persistentContainer.viewContext

        let response = Response(context: managedContext)
        response.userId = userId
        response.dateOfBirth = dateOfBirth
        response.createdAt = Date()
        response.latitude = self.latitude
        response.longitude = self.longitude

        for result in self.results ?? [] {
            let responseAnswer = ResponseAnswer(context: managedContext)
            responseAnswer.question = result.question
            responseAnswer.answer = result.answer
            responseAnswer.response = response
        }

        do {
            try managedContext.save()
        } catch let error as NSError {
            print("Could not save. \(error), \(error.userInfo)")
        }
    }

}

/// Extension for FinishViewController to add CLLocationManagerDelegate functionality.
extension FinishViewController: CLLocationManagerDelegate {
    // MARK: - Override Functions

    override func viewDidLoad() {
        self.locationManager.requestWhenInUseAuthorization()
        if CLLocationManager.locationServicesEnabled() {
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
            locationManager.startUpdatingLocation()
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        if CLLocationManager.locationServicesEnabled() {
            locationManager.startUpdatingLocation()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        if CLLocationManager.locationServicesEnabled() {
            locationManager.stopUpdatingLocation()
        }
    }

    // MARK: - ViewController Extension Functions

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location: CLLocationCoordinate2D = manager.location?.coordinate else { return }
        self.latitude = location.latitude
        self.longitude = location.longitude
    }
}
