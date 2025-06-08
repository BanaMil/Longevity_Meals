// selectable_button.dart

import 'package:flutter/material.dart';

class SelectableButton extends StatelessWidget {
  final String text;
  final bool isSelected;
  final VoidCallback onTap;
  final double fontSize;

  const SelectableButton({
    super.key,
    required this.text,
    required this.isSelected,
    required this.onTap,
    this.fontSize = 14,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 4, vertical: 6),
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
        constraints: const BoxConstraints(minHeight: 46), // 버튼의 최소 높이 
        decoration: BoxDecoration(
          color: isSelected ? const Color.fromARGB(255, 179, 216, 168) 
                            : Colors.grey[300],
          borderRadius: BorderRadius.circular(10),
        ),
        alignment: Alignment.center,
        child: Text(
          text,
          textAlign: TextAlign.center,
          style: TextStyle(fontSize:fontSize),
        ),
      ),
    );
  }
}

