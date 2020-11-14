import 'package:floating_search_bar/floating_search_bar.dart';
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
        appBar: null,
        floatingActionButton: FloatingActionButton(
          onPressed: () {
            // Add your onPressed code here!
            setState(() {
              _suggestions.add(generateWordPairs().first);
            });
          },
          child: Icon(Icons.add),
        ),
        body: Padding(
          padding: EdgeInsets.only(top: 12.0),
          child: FloatingSearchBar.builder(
            pinned: true,
            itemCount: _suggestions.length,
            itemBuilder: (BuildContext context, int index) {
              return _buildRow(_suggestions.elementAt(index));
            },
            trailing: CircleAvatar(
              child: Text("RD"),
            ),
            drawer: Drawer(
              child: Container(),
            ),
            onChanged: (String value) {},
            onTap: () {},
            decoration: InputDecoration.collapsed(
              hintText: "Search...",
            ),
          ),
        ));
  }


  Widget _buildRow(WordPair pair) {
    return PasswordCard(
      siteName: pair.first,
      userName: pair.second,
    );
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
