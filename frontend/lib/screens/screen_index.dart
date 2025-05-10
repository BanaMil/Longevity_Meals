// screen_index.dart

import 'package:flutter/material.dart';
import 'package:frontend/tabs/tab_search.dart';
import 'package:frontend/tabs/tab_home.dart';
import 'package:frontend/tabs/tab_delivery.dart';
import 'package:frontend/tabs/tab_info.dart';

class IndexScreen extends StatefulWidget {
  const IndexScreen({super.key});

  @override
  IndexScreenState createState() => IndexScreenState();
}

class IndexScreenState extends State<IndexScreen> {
  int _currentIndex = 0;
  final List<Widget> _tabs = [
    SearchTab(),
    HomeTab(),
    DeliveryTab(),
    InfoTab(),
  ];
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        iconSize: 30,
        selectedItemColor: Colors.green[200],
        unselectedItemColor: Colors.grey,
        selectedLabelStyle: TextStyle(fontSize:12),
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        items: [
          BottomNavigationBarItem(icon: Icon(Icons.search), label: '검색'),
          BottomNavigationBarItem(icon: Icon(Icons.home), label: '홈'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: '건강정보'),
          BottomNavigationBarItem(icon: Icon(Icons.local_shipping), label: '배송'),
        ],
      ),
    body: _tabs[_currentIndex],
    );
  }
}