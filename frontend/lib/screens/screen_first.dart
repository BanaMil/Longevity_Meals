// screen_first.dart

import 'package:flutter/material.dart';

class FirstScreen extends StatelessWidget {
  const FirstScreen({super.key});
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('장수밥상', style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
            SizedBox(height: 60),
            ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(context, '/screen_login');
              },
              child: Text('로그인'),
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.pushNamed(context, '/screen_register');
              },
              style: ElevatedButton.styleFrom(backgroundColor: Colors.teal[100]),
              child: Text('회원가입'),
            ),
          ],
        ),
      ),
    );
  }
}