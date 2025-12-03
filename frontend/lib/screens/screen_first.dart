// screen_first.dart

import 'package:flutter/material.dart';
import 'package:frontend/screens/screen_login.dart';
import 'package:frontend/screens/screen_register_wizard.dart';

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
            ElevatedButton(
              onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (_) => LoginScreen()));
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.white,
<<<<<<< HEAD
                minimumSize: const Size(200, 60),            
                textStyle: const TextStyle(fontSize: 20),   
=======
                minimumSize: const Size(200, 60),           // 버튼 높이 
                textStyle: const TextStyle(fontSize: 20),   // 폰트 크기
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
              ),
              child: Text('로그인', style: TextStyle(color: Colors.black)),
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (_) => RegisterWizardScreen()));
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: Color.fromARGB(156, 137, 177, 95), 
<<<<<<< HEAD
                minimumSize: const Size(200, 60),           
                textStyle: const TextStyle(fontSize: 20),   
=======
                minimumSize: const Size(200, 60),           // 버튼 높이 
                textStyle: const TextStyle(fontSize: 20),   // 폰트 크기
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
                ),
              child: Text('회원가입', style: TextStyle(color: Colors.black)),
            ),
          ],
        ),
      ),
    );
  }
}