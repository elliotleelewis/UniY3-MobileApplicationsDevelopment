//
//  ViewController.swift
//  Assignment - iOS - Photo Album
//
//  Created by Elliot Lewis on 19/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit
import CoreData
import CoreML
import Vision

/// UIViewController for the Home Screen on `Main.storyboard`
class ViewController: UIViewController {
    @IBOutlet var collection: UICollectionView!

    var managedContext: NSManagedObjectContext?

    var images: [Image] = []

    // MARK: - Override Functions

    override func viewDidLoad() {
        super.viewDidLoad()

        // Initialise managedContext for the UIViewController.
        guard let appDelegate = UIApplication.shared.delegate as? AppDelegate else { return }
        self.managedContext = appDelegate.persistentContainer.viewContext

        self.loadImages()

        // Set the dimensions of the collection view items, so they render nicely across multiple device sizes.
        let size = UIScreen.main.bounds.width / 3 - 4
        let layout = UICollectionViewFlowLayout()
        layout.sectionInset = UIEdgeInsets(top: 16, left: 0, bottom: 16, right: 0)
        layout.itemSize = CGSize(width: size, height: size)
        layout.minimumInteritemSpacing = 4
        layout.minimumLineSpacing = 4
        self.collection.collectionViewLayout = layout
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
        if segue.destination is DetailViewController {
            let controller = segue.destination as? DetailViewController
            guard let element = sender as? ImageCollectionViewCell else { return }
            controller?.image = images[element.tag]
        }
    }

    // MARK: - Actions

    /// Add image action, triggered by the Add Bar Button.
    @IBAction func addImage(_ sender: UIBarButtonItem) {
        // Setup image picker and ActionSheet alert to decide image picker source.
        let imagePicker = UIImagePickerController()
        imagePicker.delegate = self
        let alert = UIAlertController(
            title: "Add Image",
            message: "Please Select an Option",
            preferredStyle: .actionSheet
        )

        // Add actions to ActionSheet alert.
        alert.addAction(UIAlertAction(title: "Camera", style: .default, handler: { (_) in
            imagePicker.sourceType = .camera
            self.present(imagePicker, animated: true)
        }))
        alert.addAction(UIAlertAction(title: "Photo Library", style: .default, handler: { (_) in
            imagePicker.sourceType = .photoLibrary
            self.present(imagePicker, animated: true)
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))

        // Present ActionSheet alert.
        self.present(alert, animated: true)
    }

    // MARK: - ViewController Functions

    /// Loads images from CoreData.
    func loadImages() {
        let responseFetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "Image")
        responseFetchRequest.returnsObjectsAsFaults = false

        do {
            guard let images = try self.managedContext!.fetch(responseFetchRequest) as? [Image] else { return }
            self.images = images
            self.collection.reloadData()
        } catch let error as NSError {
            print("Could not load images. \(error), \(error.userInfo)")
        }
    }
}

/// Extension for ViewController to add UICollectionViewDataSource and UICollectionViewDelegate functionality.
extension ViewController: UICollectionViewDataSource, UICollectionViewDelegate {
    // MARK: - Delegate Functions

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.images.count
    }

    func collectionView(
        _ collectionView: UICollectionView,
        cellForItemAt indexPath: IndexPath
        ) -> UICollectionViewCell {
        // swiftlint:disable force_cast
        let cell = collectionView.dequeueReusableCell(
            withReuseIdentifier: "cell",
            for: indexPath
            ) as! ImageCollectionViewCell
        // Set cell data to be that of the image it represents.
        cell.image.image = UIImage(data: self.images[indexPath.row].image!)
        cell.tag = indexPath.row
        return cell
    }
}

/// Extension for ViewController to add UIImagePickerControllerDelegate and UINavigationControllerDelegate
/// functionality.
extension ViewController: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    // MARK: - Delegate Functions

    func imagePickerController(
        _ picker: UIImagePickerController,
        didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]
        ) {
        // Dismiss the image picker and get image selected from it.
        self.dismiss(animated: true, completion: nil)
        guard let selectedImage = info[.originalImage] as? UIImage else { return }

        // Initialise the CoreData image object.
        let image = Image(context: self.managedContext!)
        image.title = selectedImage.description
        image.image = selectedImage.jpegData(compressionQuality: 1)

        // Predict what the image is.
        do {
            let modelLibrary = MobileNet()
            let model = try VNCoreMLModel(for: modelLibrary.model)
            let handler = VNImageRequestHandler(data: image.image!)
            let request = VNCoreMLRequest(model: model, completionHandler: { (request, _) in
                guard let results = request.results as? [VNClassificationObservation] else {
                    fatalError("Could not get image identification results.")
                }
                for classification in results where classification.confidence > image.predictionConfidence {
                    image.prediction = classification.identifier
                    image.predictionConfidence = classification.confidence
                }
            })
            try handler.perform([request])
        } catch let error as NSError {
            print("Could not identify image. \(error), \(error.userInfo)")
        }

        // Save the image object into CoreData.
        do {
            try self.managedContext!.save()
        } catch let error as NSError {
            print("Could not save. \(error), \(error.userInfo)")
        }

        // Refresh the UI to show the newly added image.
        self.loadImages()
    }
}
