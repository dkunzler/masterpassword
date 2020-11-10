import 'package:flutter/material.dart';
import 'package:english_words/english_words.dart';
import 'package:masterpassword2/PasswordCard.dart';
import 'package:masterpassword2/login.dart';

void main() => runApp(MyApp());

class PasswordList extends StatefulWidget {
  @override
  _PasswordListState createState() => _PasswordListState();
}

class _PasswordListState extends State<PasswordList> {
  final _currentCategory = "Passwords";
  final _suggestions = <WordPair>[];
  final _biggerFont = TextStyle(fontSize: 18.0);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_currentCategory),
      ),
      body: _buildSuggestions(),
    );
  }

  Widget _buildSuggestions() {
    return ListView.builder(
        padding: EdgeInsets.all(8.0),
        itemBuilder: /*1*/ (context, i) {
          final index = i ~/ 2; /*3*/
          if (index >= _suggestions.length) {
            _suggestions.addAll(generateWordPairs().take(10)); /*4*/
          }
          return _buildRow(_suggestions[index]);
        });
  }

  Widget _buildRow(WordPair pair) {
    return PasswordCard(siteName: pair.first, userName: pair.second,);
  }
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        theme: ThemeData.from(
            colorScheme: ColorScheme.fromSwatch(
                primarySwatch: Colors.blueGrey,
                accentColor: Colors.green,
                backgroundColor: Colors.white)),
        title: 'Passwords',
        home: Scaffold(
          appBar: null,
          body: Login(),
        ));
  }
}
