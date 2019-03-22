//
//  QuestionViewController.swift
//  Assignment - iOS - Questionnaire
//
//  Created by Elliot Lewis on 04/03/2019.
//  Copyright © 2019 Elliot Lewis. All rights reserved.
//

import UIKit

/// UIViewController for the Question Screen on `Main.storyboard`
class QuestionViewController: UIViewController {
    @IBOutlet weak var questionTitle: UILabel!
    @IBOutlet weak var question: UILabel!
    @IBOutlet weak var answers: UIStackView!

    var userId: String?
    var dateOfBirth: Date?

    var questionCount = 1
    var currentQuestion = 0
    var questions: [Question] = [
        Question(question: "Have you used an iPad before?", answers: [
            Answer(answer: "Yes", nextQuestion: 1),
            Answer(answer: "No", nextQuestion: 2)
            ]),
        Question(question: "What is your main use of the iPad? (select one)", answers: [
            Answer(answer: "Work", nextQuestion: 3),
            Answer(answer: "Games", nextQuestion: 3),
            Answer(answer: "Internet Browsing", nextQuestion: 3),
            Answer(answer: "Communication or Social Networking", nextQuestion: 3),
            Answer(answer: "Other", nextQuestion: 3)
            ]),
        Question(question: "After using the iPad today, how would you rate its usability?", answers: [
            Answer(answer: "Very Easy", nextQuestion: 3),
            Answer(answer: "Easy", nextQuestion: 3),
            Answer(answer: "Neither Easy or Difficult", nextQuestion: 3),
            Answer(answer: "Difficult", nextQuestion: 3),
            Answer(answer: "Very Difficult", nextQuestion: 3)
            ]),
        Question(question: "Would you ever consider using an iPad again in the future?", answers: [
            Answer(answer: "Yes", nextQuestion: -1),
            Answer(answer: "No", nextQuestion: -1)
            ])
    ]
    var results: [Result] = []

    // MARK: - Override Functions

    override func viewDidLoad() {
        super.viewDidLoad()
        self.updateView()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
        if segue.destination is FinishViewController {
            let controller = segue.destination as? FinishViewController
            controller?.userId = self.userId
            controller?.dateOfBirth = self.dateOfBirth
            controller?.results = self.results
        }
    }

    // MARK: - Actions

    /// Action that is run whenever a question is answered. If there are any more questions to be displayed, then
    /// display them, otherwise perform a segue to FinishViewController.
    ///
    /// - parameters:
    ///   - sender: Button that called this action.
    @IBAction func answerQuestion(sender: UIButton) {
        let question = self.questions[self.currentQuestion]
        let answer = question.answers[sender.tag]
        results.append(Result(question: question.question, answer: answer.answer))
        if answer.nextQuestion == -1 {
            self.performSegue(withIdentifier: "complete", sender: self)
        } else {
            self.currentQuestion = answer.nextQuestion
            self.questionCount += 1
            self.updateView()
        }
    }

    // MARK: - ViewController Functions

    func updateView() {
        self.questionTitle.text = "Question " + self.questionCount.description
        self.question.text = self.questions[currentQuestion].question
        self.answers.subviews.forEach({ $0.removeFromSuperview() })
        for (index, answer) in self.questions[self.currentQuestion].answers.enumerated() {
            let button = UIButton(type: .roundedRect)
            button.setTitle(answer.answer, for: .normal)
            button.tag = index
            button.addTarget(self, action: #selector(self.answerQuestion), for: .touchUpInside)
            button.titleLabel?.font = UIFont.systemFont(ofSize: 24)
            button.titleLabel?.numberOfLines = 0
            button.titleLabel?.lineBreakMode = NSLineBreakMode.byWordWrapping
            button.titleLabel?.textAlignment = .center
            self.answers.addArrangedSubview(button)
        }
    }
}
