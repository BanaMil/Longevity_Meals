// register_progress_bar.dart

import 'package:flutter/material.dart';

class RegisterProgressBar extends StatelessWidget {
  final int step;       // 0..4
  final int total;      // 5
  const RegisterProgressBar({super.key, required this.step, this.total = 5});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: List.generate(total * 2 - 1, (i) {
        if (i.isOdd) {
          // 연결선
          final segIndex = (i ~/ 2);
          final active = segIndex < step;
          return Expanded(
            child: Container(
              height: 8,
              margin: const EdgeInsets.symmetric(horizontal: 4),
              decoration: BoxDecoration(
                color: active ? Color.fromARGB(255, 66, 105, 50) : Colors.black26,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          );
        } else {
          // 원형 단계 표시
          final idx = (i ~/ 2); // 0..4
          final active = idx < step;
          final current = idx == step;

          return Container(
            width: 36, height: 36,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: current ? Color.fromARGB(255, 66, 105, 50) 
                  : (active ? Color.fromARGB(255, 66, 105, 50) : Colors.white),
              border: Border.all(
                color: current ? Color.fromARGB(255, 66, 105, 50) : Colors.black54,
                width: 2,
              ),
            ),
            alignment: Alignment.center,
            child: Text(
              '${idx+1}',
              style: TextStyle(
                color: current ? Colors.white : (active ? Colors.white : Colors.black87),
                fontWeight: FontWeight.bold,
                fontSize: 18,
              ),
            ),
          );
        }
      }),
    );
  }
}
