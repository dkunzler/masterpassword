import 'dart:ui';

import 'package:flutter/material.dart';

class PasswordCard extends StatefulWidget {
  final String siteName;
  final String userName;

  const PasswordCard({Key key, this.siteName, this.userName}) : super(key: key);

  @override
  _PasswordCardState createState() => _PasswordCardState();
}

class _PasswordCardState extends State<PasswordCard> {
  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: EdgeInsets.all(16.0),
        child: Row(
          mainAxisSize: MainAxisSize.max,
          children: [
            Image(
              image: AssetImage('assets/graphics/ic_go.png'),
              height: 28,
              width: 28,
            ),
            Padding(padding: EdgeInsets.only(left: 10.0)),
            Expanded(
                child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(widget.siteName,
                    style: TextStyle(fontWeight: FontWeight.bold)),
                Text(widget.userName),
              ],
            )),
            Icon(Icons.more_vert),
          ],
        ),
      ),
    );
  }
}
