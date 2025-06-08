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
  int _currentIndex = 1;
  final List<Widget> _tabs = [SearchTab(), HomeTab(), DeliveryTab(), InfoTab()];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _tabs[_currentIndex],
      bottomNavigationBar: Container(
        decoration: const BoxDecoration(
          color: Color.fromARGB(255, 251, 255, 228),
          borderRadius: BorderRadius.only(
            topLeft: Radius.circular(20),
            topRight: Radius.circular(20),
          ),
          boxShadow: [
            BoxShadow(
              color: Colors.grey,
              blurRadius: 5,
              offset: Offset(0, -2),
            ),
          ],
        ),
        child: ClipRRect(
          borderRadius: const BorderRadius.only(
            topLeft: Radius.circular(20),
            topRight: Radius.circular(20),
          ),
          child: BottomNavigationBar(
            type: BottomNavigationBarType.fixed,
            backgroundColor: Colors.transparent,
            elevation: 0,
            iconSize: 40,
            selectedItemColor: Color.fromARGB(255, 86, 128, 52),
            unselectedItemColor: Colors.grey[500],
            selectedLabelStyle: TextStyle(fontSize: 12),
            currentIndex: _currentIndex,
            onTap: (index) {
              setState(() {
                _currentIndex = index;
              });
            },
            items: [
              BottomNavigationBarItem(icon: Icon(Icons.search), label: '검색'),
              BottomNavigationBarItem(icon: Icon(Icons.home), label: '홈'),
              BottomNavigationBarItem(icon: Icon(Icons.local_shipping), label: '배송',),
              BottomNavigationBarItem(icon: Icon(Icons.person), label: '내정보'),
            ],
          ),
        ),
      ),
    );
  }
}
