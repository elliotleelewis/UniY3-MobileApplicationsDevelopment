//
//  Result.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 04/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import Foundation

/// Result model
class Result {
    var question: String
    var answer: String

    init(question: String, answer: String) {
        self.question = question
        self.answer = answer
    }
}
