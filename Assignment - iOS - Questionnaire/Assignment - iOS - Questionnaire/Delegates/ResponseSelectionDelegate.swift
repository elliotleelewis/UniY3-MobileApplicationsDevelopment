//
//  AdminResponseSelectionDelegate.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 17/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import Foundation

/// Delegate for handling selected response actions in AdminSplitViewController
protocol ResponseSelectionDelegate: class {
    /// Selected response action
    ///
    /// - parameters:
    ///   - response: Response object that was selected
    func responseSelected(_ response: Response)
}
