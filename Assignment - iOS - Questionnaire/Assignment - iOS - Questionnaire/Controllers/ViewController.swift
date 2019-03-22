//
//  ViewController.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 28/02/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit

/// UIViewController for the Home Screen on `Main.storyboard`
class ViewController: UIViewController {
    @IBOutlet weak var userId: UITextField!
    @IBOutlet weak var dateOfBirth: UIDatePicker!

    // MARK: - Override Functions

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
        print("UserID: " + (userId.text ?? ""))
        print("DoB: " + dateOfBirth.date.description)
        if segue.destination is QuestionViewController {
            let controller = segue.destination as? QuestionViewController
            controller?.userId = userId.text
            controller?.dateOfBirth = dateOfBirth.date
        }
    }

    // MARK: - Actions

    /// Unwind function for making a segue back to this UIViewController.
    ///
    /// - parameters:
    ///   - segue: Segue that is unwinding.
    @IBAction func unwind(segue: UIStoryboardSegue) {}
}
