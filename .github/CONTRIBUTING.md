# Contributing to `twilio-verify-passkeys`

Thank you for your interest in contributing to `twilio-verify-passkeys`. We appreciate your help in making it better. Please take a moment to review these guidelines before getting started.

- [Branches](#branches)
- [Code of Conduct](#code-of-conduct)
- [Question or Problem?](#question-or-problem)
- [Issues and Bugs](#issues-and-bugs)
- [Feature Requests](#feature-requests)
- [Documentation Fixes](#documentation-fixes)
- [Submission Guidelines](#submission-guidelines)
- [Coding Rules](#coding-rules)
- [License](#license)

## Branches

- **`main`**: The `main` branch is the stable branch containing production-ready code. It should always reflect the latest release.
- **`dev`**: The `dev` branch is where all new features and changes should be directed. This branch might contain work-in-progress code.
- Any other branch contains code with new features or improvements that are waiting to be merged into the `dev` branch.

## Code of Conduct

Help us keep `twilio-verify-passkeys` open and inclusive. Please be kind to and considerate of other developers, as we all have the same goal: make `twilio-verify-passkeys` as good as it can be.
By interacting with the project in any shape or from you are agreeing to the project's [Code of Conduct](https://github.com/twilio/twilio-verify-passkeys?tab=coc-ov-file). If you feel like another individual has violated the code of conduct, please raise a complaint to [open-source@twilio.com](mailto:open-source@twilio.com).

## Question or Problem?

If you have questions about how to use `twilio-verify-passkeys`, please see our [docs](../README.md), and if you don't find the answer there, please contact [Twilio Support](https://www.twilio.com/help/contact) with any issues you have.

## Issues and Bugs

If you find a bug in the source code or a mistake in the documentation, you can  help us by submitting [an issue][issue-link].

**Please see the [Submission Guidelines](#submission-guidelines) below.**

## Feature Requests

You can request a new feature by submitting an issue to our [GitHub Repository][github]. If you would like to implement a new feature then consider what kind of change it is:

* **Major Changes** that you wish to contribute to the project should be
  discussed first with `twilio-verify-passkeys` contributors in an issue or pull request so
  that we can develop a proper solution and better coordinate our efforts,
  prevent duplication of work, and help you to craft the change so that it is
  successfully accepted into the project.
* **Small Changes** can be crafted and submitted to the
  [GitHub Repository][github] as a Pull Request.

## Documentation Fixes

If you want to help improve the documentation, it's a good idea to
let others know what you're working on to minimize duplication of effort. Create
a new issue (or comment on a related existing one) to let others know what
you're working on.

For large fixes, please build and test the documentation before submitting the
PR to be sure you haven't accidentally introduced layout or formatting issues.

## Submission Guidelines

### Submitting an Issue
Before you submit your issue search the archive, maybe your question was already
answered.

If your issue appears to be a bug, and hasn't been reported, open a new issue.
Help us to maximize the effort we can spend fixing issues and adding new
features by not reporting duplicate issues. Providing the following information
will increase the chances of your issue being dealt with quickly:

* **Overview of the Issue** - if an error is being thrown a non-minified stack
  trace helps
* **Motivation for or Use Case** - explain why this is a bug for you
* **`twilio-verify-passkeys` Version(s)** - is it a regression?
* **Operating System** - is this a problem with all systems or
  only specific ones?
* **Reproduce the Error** - provide an isolated code snippet or an unambiguous
  set of steps.
* **Related Issues** - has a similar issue been reported before?
* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point
  to what might be causing the problem (line of code or commit)

**If you get help, help others. Good karma rules!**

### Submitting a Pull Request
Before you submit your pull request consider the following guidelines:

* Search [GitHub][github] for an open or closed Pull Request that relates to
  your submission. You don't want to duplicate effort.
* Make your changes in a new git branch from `dev` branch:

    ```bash
    git checkout -b my-fix-branch dev
    ```

* Create your patch, **including appropriate test cases**.
* Follow our [Coding Rules](#coding-rules).
* Commit your changes using a descriptive commit message.
  We follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification for our commit messages. Please ensure your commits adhere to this convention to maintain a clean and structured commit history.

    ```bash
    git commit -a
    ```
  Note: the optional commit `-a` command line option will automatically "add"
  and "rm" edited files.

* Build your changes locally to ensure all the tests pass:

    ```bash
    ./gradlew :shared:iosSimulatorArm64Test :shared:testDebugUnitTest
    ```

* Make sure you're following the code convention:

  * **Ktlint**
    We use [ktlint](https://ktlint.github.io/) for enforcing Kotlin coding standards. Before submitting a pull request, please ensure your code complies with ktlint rules. You can run ktlint locally with:

    ```bash
    ./gradlew ktlintCheck
    ```

  * **Detekt**
  We use [Detekt](https://detekt.dev/) for static code analysis and enforcing coding standards. Please ensure your code passes Detekt checks before submitting a pull request. You can run Detekt locally with:

    ```bash
    ./gradlew detekt
    ```
    
* Push your branch to GitHub:

    ```bash
    git push origin my-fix-branch
    ```

In GitHub, send a pull request to `twilio-verify-passkeys:dev`.
If we suggest changes, then:

* Make the required updates.
* Re-run the `twilio-verify-passkeys` test suite to ensure tests are still passing.
* Commit your changes to your branch (e.g. `my-fix-branch`).
* Push the changes to your GitHub repository (this will update your Pull Request).

That's it! Thank you for your contribution!

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull
the changes from the main (upstream) repository.

## Coding Rules

To ensure consistency throughout the source code, keep these rules in mind as
you are working:

* All features or bug fixes **must be tested** by one or more tests.
* All classes and methods **must be documented**.
* All classes must follow the code convention

## License
All third party contributors acknowledge that any contributions they provide will be made under the same open source license that the open source project provided under.

[issue-link]: https://github.com/twilio/twilio-verify-passkeys/issues/new
[github]: https://github.com/twilio/twilio-verify-passkeys
