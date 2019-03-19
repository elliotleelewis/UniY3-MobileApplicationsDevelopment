//
//  AdminMasterViewController.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 12/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit
import CoreData

class AdminMasterViewController: UITableViewController {

    weak var delegate: ResponseSelectionDelegate?

    var responses: [Response] = []

    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        self.clearsSelectionOnViewWillAppear = false
    }

    override func viewWillAppear(_ animated: Bool) {
        loadResponses()
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.responses.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        let response = self.responses[indexPath.row]
        cell.textLabel?.text = response.userId! + " - " + response.createdAt!.description
        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let response = self.responses[indexPath.row]
        delegate?.responseSelected(response)
    }

    func loadResponses() {
        guard let appDelegate = UIApplication.shared.delegate as? AppDelegate else { return }
        let managedContext = appDelegate.persistentContainer.viewContext
        let responseFetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "Response")
        responseFetchRequest.returnsObjectsAsFaults = false

        do {
            guard let responses = try managedContext.fetch(responseFetchRequest) as? [Response] else { return }
            self.responses = responses
            self.tableView.reloadData()
        } catch {
            print("Failed!")
        }
    }

}
