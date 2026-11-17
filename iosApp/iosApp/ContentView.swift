
import UIKit
import SwiftUI
import Shared
import MessageUI

class MessageComposer: NSObject, MFMessageComposeViewControllerDelegate {

    static let shared = MessageComposer()

    func sendMessage(
        phoneNumber: String,
        message: String,
        from viewController: UIViewController
    ) {
        guard MFMessageComposeViewController.canSendText() else {
            let alert = UIAlertController(
                title: "SMS Not Available",
                message: "SMS messaging is not supported on this device. Please test this feature on a physical iPhone with SMS capability.",
                preferredStyle: .alert
            )

            alert.addAction(UIAlertAction(title: "OK", style: .default))
            viewController.present(alert, animated: true)
            return
        }

        let composer = MFMessageComposeViewController()
        composer.recipients = [phoneNumber]
        composer.body = message
        composer.messageComposeDelegate = self

        viewController.present(composer, animated: true)
    }

    func messageComposeViewController(
        _ controller: MFMessageComposeViewController,
        didFinishWith result: MessageComposeResult
    ) {
        controller.dismiss(animated: true)
    }
}

struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Self.Context) -> UIViewController {

        MainViewControllerKt.MainViewController(
            onPrepareSms: { phoneNumber, message in

                let windowScene = UIApplication.shared.connectedScenes
                    .compactMap { $0 as? UIWindowScene }
                    .first

                let rootController = windowScene?.windows
                    .first(where: { $0.isKeyWindow })?
                    .rootViewController

                if let controller = rootController {
                    MessageComposer.shared.sendMessage(
                        phoneNumber: phoneNumber,
                        message: message,
                        from: controller
                    )
                }
            }
        )
    }

    func updateUIViewController(
        _ uiViewController: UIViewController,
        context: Self.Context
    ) {}
}

struct ContentView: View {

    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}

