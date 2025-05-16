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
    this.fontSize = 20,
  });

  @override
  Widget build(BuildContext context) {
    final double buttonWidth = (MediaQuery.of(context).size.width - (6 * 2 * 5)) / 5;  // margin:6, 5개 버튼 
   
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: buttonWidth,
        margin: const EdgeInsets.all(6),
        padding: const EdgeInsets.symmetric(vertical: 10),
        decoration: BoxDecoration(
          color: isSelected ? Colors.green[200] : Colors.grey[300],
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

