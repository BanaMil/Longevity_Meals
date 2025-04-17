// input_diseases.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'package:frontend/widgets/selectable_button.dart';

class InputDiseasesScreen extends StatelessWidget {
    const InputDiseasesScreen({super.key});

    final List<String> diseaseList = const [
        '고혈압', '고지혈증', '관절염', '당뇨병',
        '심장질환', '천식', '비만', '해당없음',
    ];

    @override
    Widget build(BuildContext context) {
        final provider = context.watch<HealthInfoProvider>();
        return Scaffold(
            appBar: AppBar(title: const Text('질병 선택')),
            body: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                    const Padding(
                        padding: EdgeInsets.all(16),
                        child: Text('질병을 선택해주세요.'),
                    ),
                    Wrap(
                        children: diseaseList.map((d) {
                            final selected = provider.info.diseases.contains(d);
                            return SelectableButton(
                                text: d,
                                isSelected: selected,
                                onTap: () => provider.toggleDisease(d),
                            );
                        }).toList(),
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
                                onPressed: () => Navigator.pushNamed(context, '/input_allergies'),
                            ),    
                        ],
                    )
                ],
            ),
        );
    }
}