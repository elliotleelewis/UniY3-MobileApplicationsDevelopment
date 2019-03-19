//
//  Answer.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 04/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import Foundation

class Answer {
    var answer: String
    var nextQuestion: Int

    init(answer: String, nextQuestion: Int) {
        self.answer = answer
        self.nextQuestion = nextQuestion
    }
}
