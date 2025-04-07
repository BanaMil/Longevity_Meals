// screen_index.dart
import 'package:flutter/material.dart';
import 'package:frontend/tabs/tab_shop.dart';
import 'package:frontend/tabs/tab_home.dart';
import 'package:frontend/tabs/tab_info.dart';
import 'package:frontend/tabs/tab_setting.dart';

class IndexScreen extends StatefulWidget {
  @override
  _IndexScreenState createState() => _IndexScreenState();
}

class _IndexScreenState extends State<IndexScreen> {
  int _currentIndex = 0;
  final List<Widget> _tabs = [
    ShopTab(),
    HomeTab(),
    InfoTab(),
    SettingTab(),
  ];
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        iconSize: 30,
        selectedItemColor: Colors.blue,
        unselectedItemColor: Colors.grey,
        selectedLabelStyle: TextStyle(fontSize:12),
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        items: [
          BottomNavigationBarItem(icon: Icon(Icons.shopping_bag), label: '쇼핑'),
          BottomNavigationBarItem(icon: Icon(Icons.home), label: '홈'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: '건강정보'),
          BottomNavigationBarItem(icon: Icon(Icons.settings), label: '설정'),
        ],
      ),
    body: _tabs[_currentIndex],
    );
  }
}