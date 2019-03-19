//
//  AdminDetailViewController.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 17/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit
import MapKit

class AdminDetailViewController: UIViewController, ResponseSelectionDelegate {
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var map: MKMapView!

    var response: Response?

    func responseSelected(_ response: Response) {
        self.response = response
        self.stackView.subviews
            .filter({ !($0 is MKMapView) })
            .forEach({ $0.removeFromSuperview() })
        self.map.removeAnnotations(self.map.annotations)
        for case let (index, answer as ResponseAnswer) in self.response!.answers!.enumerated() {
            let questionLabel = UILabel(frame: CGRect(x: 0, y: 0, width: self.stackView.frame.width, height: 24))
            questionLabel.text = answer.question
            questionLabel.numberOfLines = 0
            questionLabel.lineBreakMode = .byWordWrapping
            questionLabel.textAlignment = .center
            questionLabel.font = UIFont.boldSystemFont(ofSize: questionLabel.font.pointSize)
            let answerLabel = UILabel(frame: CGRect(x: 0, y: 0, width: self.stackView.frame.width, height: 24))
            answerLabel.text = answer.answer
            answerLabel.numberOfLines = 0
            answerLabel.lineBreakMode = .byWordWrapping
            answerLabel.textAlignment = .center
            self.stackView.insertArrangedSubview(questionLabel, at: index * 2)
            self.stackView.insertArrangedSubview(answerLabel, at: (index * 2) + 1)
        }
        let coordinates = CLLocationCoordinate2D(
            latitude: (self.response?.latitude)!,
            longitude: (self.response?.longitude)!
        )
        self.map.setCenter(coordinates, animated: true)
        let marker = MKPointAnnotation()
        marker.title = "Response"
        marker.coordinate = coordinates
        map.addAnnotation(marker)
    }
}
