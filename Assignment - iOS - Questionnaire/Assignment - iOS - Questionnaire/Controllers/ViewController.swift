//
//  ViewController.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 28/02/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    @IBOutlet weak var userId: UITextField!
    @IBOutlet weak var dateOfBirth: UIDatePicker!

    @IBAction func unwind(segue: UIStoryboardSegue) {
        //
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

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
}
