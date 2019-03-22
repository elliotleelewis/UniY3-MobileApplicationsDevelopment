//
//  AdminSplitViewController.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 11/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit
import LocalAuthentication

/// UISplitViewController for the Admin Split Screen on `Main.storyboard`
class AdminSplitViewController: UISplitViewController, UISplitViewControllerDelegate {
    // MARK: - Override Functions

    override func viewDidLoad() {
        super.viewDidLoad()
        self.delegate = self
        super.preferredDisplayMode = .allVisible
    }

    override func viewDidAppear(_ animated: Bool) {
        let authContext = LAContext()
        var authError: NSError?

        let alert = UIAlertController(
            title: "Error",
            message: "You're not allowed to view this.",
            preferredStyle: .alert
        )
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))

        if authContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &authError) {
            // If device has biometric security, authenticate using it
            authContext.evaluatePolicy(
                .deviceOwnerAuthenticationWithBiometrics,
                localizedReason: "Authenticate for access to Admin content."
            ) { success, _ in
                DispatchQueue.main.async {
                    if !success {
                        self.tabBarController?.selectedIndex = 0
                        self.present(alert, animated: true, completion: nil)
                    }
                }
            }
        } else if authContext.canEvaluatePolicy(.deviceOwnerAuthentication, error: &authError) {
            // If device doesn't have biometric security, but does have security, authenticate using it
            authContext.evaluatePolicy(
                .deviceOwnerAuthentication,
                localizedReason: "Authenticate for access to Admin content."
            ) { success, _ in
                DispatchQueue.main.async {
                    if !success {
                        self.tabBarController?.selectedIndex = 0
                        self.present(alert, animated: true, completion: nil)
                    }
                }
            }
        } else {
            DispatchQueue.main.async {
                self.tabBarController?.selectedIndex = 0
                self.present(alert, animated: true, completion: nil)
            }
        }
    }

    func splitViewController(
        _ splitViewController: UISplitViewController,
        collapseSecondary secondaryViewController: UIViewController,
        onto primaryViewController: UIViewController
        ) -> Bool {
        // Return true to prevent UIKit from applying its default behavior
        return true
    }
}
