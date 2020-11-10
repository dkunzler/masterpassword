import 'package:flutter/material.dart';
import 'package:masterpassword2/main.dart';

class Login extends StatefulWidget {
  @override
  _LoginState createState() => _LoginState();
}

class _LoginState extends State<Login> {
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return Center(
        child: Padding(
      padding: EdgeInsets.all(16.0),
      child: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextFormField(
              validator: (value) {
                if (value.isEmpty) {
                  return 'Name cannot be empty';
                }
                return null;
              },
              decoration: InputDecoration(
                // border: OutlineInputBorder(),
                labelText: 'Name',
              ),
            ),
            Padding(
                padding: EdgeInsets.symmetric(vertical: 16.0),
                child: TextFormField(
                  validator: (value) {
                    if (value.isEmpty) {
                      return 'Password cannot be empty';
                    }
                    return null;
                  },
                  obscureText: true,
                  decoration: InputDecoration(
                    // border: OutlineInputBorder(),
                    labelText: 'Password',
                  ),
                )),
            Padding(
                padding: EdgeInsets.symmetric(vertical: 30.0),
                child: Center(
                  child: IconButton(
                    iconSize: 42,
                    onPressed: () {
                      if (_formKey.currentState.validate()) {
                        Navigator.pushReplacement(
                          context,
                          MaterialPageRoute(
                              builder: (context) => PasswordList()),
                        );
                      }
                    },
                    icon: Image(image: AssetImage('assets/graphics/ic_go.png')),
                  ),
                )),
          ],
        ),
      ),
    ));
  }
}
