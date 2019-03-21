//
//  DetailViewController.swift
//  Assignment - iOS - Photo Album
//
//  Created by Elliot Lewis on 19/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit
import CoreData

class DetailViewController: UIViewController {
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var prediction: UILabel!
    @IBOutlet weak var predictionConfidence: UILabel!

    var image: Image?

    override func viewDidLoad() {
        super.viewDidLoad()

        self.imageView.image = UIImage(data: (image?.image)!)
        self.prediction.text = image?.prediction
        self.predictionConfidence.text = image?.predictionConfidence.description
    }
}
