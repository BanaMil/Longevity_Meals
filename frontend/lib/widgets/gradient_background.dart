// gradient_background.dart

import 'package:flutter/material.dart';

class GradientBackground extends StatelessWidget {
  final Widget child;

  const GradientBackground({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.bottomCenter,
          end: Alignment.topCenter,
          colors: [
            Color.fromARGB(128, 251, 255, 228), // 아래쪽 색
            Color.fromARGB(128, 196, 215, 110), // 중간 색
            Color.fromARGB(128, 135, 177, 95), // 위쪽 색
          ],
          stops: [0, 0.3, 0.9] // 각 색상의 위치 
        ),
      ),
      child: child,
    );
  }
}