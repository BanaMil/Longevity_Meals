// input_height_weight.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';


class InputHeightWeightScreen extends StatefulWidget {
    const InputHeightWeightScreen({super.key});

    @override
    State<InputHeightWeightScreen> createState() => _InputHeightWeightScreenState();
}

class _InputHeightWeightScreenState extends State<InputHeightWeightScreen> {
    final heightController = TextEditingController();
    final weightController = TextEditingController();

    @override
    Widget build(BuildContext context) {
        return Scaffold(
            appBar: AppBar(title: const Text('건강정보 입력')),
            body: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                        const Padding(
                          padding: EdgeInsets.symmetric(vertical: 8),
                          child: Text('키를 입력해주세요.'),
                        ),
                        TextField(
                            controller: heightController,
                            decoration: const InputDecoration(labelText: '키 (cm)'),
                            keyboardType: TextInputType.number,
                        ),
                        const Padding(
                          padding: EdgeInsets.symmetric(vertical: 8),
                          child: Text('몸무게를 입력해주세요.'),
                        ),
                        TextField(
                            controller: weightController,
                            decoration: const InputDecoration(labelText: '몸무게 (kg)'),
                            keyboardType: TextInputType.number,
                        ),
                        const Spacer(),
                        Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                                IconButton(
                                    icon: const Icon(Icons.arrow_back),
                                    onPressed: () => Navigator.pop(context),
                                ),
                                IconButton(
                                    icon: const Icon(Icons.arrow_forward),
                                    onPressed: () {
                                        final h = double.tryParse(heightController.text);
                                        final w = double.tryParse(weightController.text);
                                        if (h != null && w != null) {
                                            context.read<HealthInfoProvider>().setHeight(h);
                                            context.read<HealthInfoProvider>().setWeight(w);
                                            Navigator.push(
                                              context,
                                              MaterialPageRoute(builder: (context) => const InputDiseasesScreen()), //카메라 화면 생성하기
                                            );
                                          }
                                    },
                                ),
                            ],
                        )
                    ],
                ), 
            ),
        );
    }
}
