// screen_first.dart

import 'package:flutter/material.dart';
import 'package:frontend/screens/screen_login.dart';
import 'package:frontend/screens/screen_register.dart';
// import 'package:frontend/screens/screen_health_info/choice_info.dart';

class FirstScreen extends StatelessWidget {
  const FirstScreen({super.key});
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 196, 215, 108),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ClipOval(
              child: Image.asset(
              'assets/images/longevity_meals_logo.png',
              width: 250,
              height: 250,
              fit: BoxFit.cover,
              ),
            ),
            const SizedBox(height: 60), 
            // SizedBox(height: 20),
            // Text('장수밥상', style: TextStyle(fontSize: 40, fontWeight: FontWeight.bold)),
            ElevatedButton(
              onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (_) => LoginScreen()));
                // Navigator.pushNamed(context, '/screen_login');
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.white,
                minimumSize: const Size(200, 60),           // 버튼 높이 
                textStyle: const TextStyle(fontSize: 20),   // 폰트 크기
              ),
              child: Text('로그인', style: TextStyle(color: Colors.black)),
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (_) => RegisterScreen()));
                // Navigator.pushNamed(context, '/screen_register');
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: Color.fromARGB(156, 137, 177, 95), 
                minimumSize: const Size(200, 60),           // 버튼 높이 
                textStyle: const TextStyle(fontSize: 20),   // 폰트 크기
                ),
              child: Text('회원가입', style: TextStyle(color: Colors.black)),
            ),
          ],
        ),
      ),
    );
  }
}