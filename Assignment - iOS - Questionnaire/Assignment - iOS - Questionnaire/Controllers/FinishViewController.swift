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

class FinishViewController: UIViewController, CLLocationManagerDelegate {
    var locationManager = CLLocationManager()
    var latitude: Double = 0
    var longitude: Double = 0

    var userId: String?
    var dateOfBirth: Date?
    var results: [Result]?

    @IBAction func finish(_ sender: Any) {
        saveResponse()
        performSegue(withIdentifier: "finishSegue", sender: self)
    }

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

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location: CLLocationCoordinate2D = manager.location?.coordinate else { return }
        self.latitude = location.latitude
        self.longitude = location.longitude
    }

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
