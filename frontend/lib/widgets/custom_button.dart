// custom_button.dart

import 'package:flutter/material.dart';

class CustomButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final double fontSize;
  final IconData? icon;

  const CustomButton({
    super.key,
    required this.text,
    required this.onPressed,
    this.fontSize = 16,
    this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(6),
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      child: ElevatedButton(
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: Color.fromARGB(255, 235, 239, 165), // 고정 배경색
          foregroundColor: Colors.grey[800], // 텍스트 색
          // side: const BorderSide(color: Colors.black, width: 0.5), // 태두리
          elevation: 2,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
          ),
          padding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 14,
          ), // 버튼 내부 공간
        ),
        child: icon == null
          ? Text(text, style: TextStyle(fontSize: fontSize))
          : Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(icon, size: fontSize),
              const SizedBox(width: 6),
              Flexible(
                child: Text(
                  text,
                  style: TextStyle(fontSize: fontSize),
                  overflow: TextOverflow.ellipsis,
                  softWrap: false,
                ),
              ),
            ],
          ),
      ),
    );
  }
}
