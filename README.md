# fancyComposeCalculator

This project is a simple calculator that can perform basic operations like
addition, subtraction, multiplication, division with Integers and Floating-Point
values. It is implemented using the Model-View-ViewModel (MVVM) architecture
pattern and Jetpack Compose UI toolkit.

Furthermore it is setup with coding style guide and automated code formatting
using ktlint and ktlint-gradle plugin. It contains a custom Compose ruleset.
Additional rules can be found in the `.editorconfig` file in the root directory.

Calculations are done with the Shunting-yard algorithm. The algorithm is used to
convert infix expressions to postfix expressions. The postfix expression is then
evaluated to get the result. The algorithm is implemented in the
`CalculationUtil` class. It supports the following operators: `+`, `-`, `*`,
`/`, `(`. `)`.

Additionally the app has a history feature that stores the last calculations in
the reactive `DataStore`. The UI is updated whenever a new calculation is added
to the history. Users can clear the history by clicking the delete button. Items
in the list can be clicked to copy the result to the output field.

Some basic unit tests are added for checking the calculation logic.

![Screenshot_20250205_151536_small](https://github.com/user-attachments/assets/344caffd-b293-4a77-914b-0694be8de4a6)
![Screenshot_20250205_151408_small](https://github.com/user-attachments/assets/e81ccd58-3ea6-43b6-a3d7-7f56cf709438)
