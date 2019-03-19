//
//  Question.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 04/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import Foundation

class Question {
    var question: String
    var answers: [Answer]

    init(question: String, answers: [Answer]) {
        self.question = question
        self.answers = answers
    }
}
