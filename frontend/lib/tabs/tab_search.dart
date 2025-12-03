// tab_search.dart

import 'package:flutter/material.dart';
import 'package:frontend/widgets/gradient_background.dart';
import 'package:frontend/services/service_search_recipe.dart';
import 'package:frontend/models/food_item.dart';
import 'package:frontend/screens/Search/screen_results_view.dart';

class SearchTab extends StatefulWidget {
  const SearchTab({super.key});

  @override
  State<SearchTab> createState() => _SearchTabState();
}

class _SearchTabState extends State<SearchTab> {
  final TextEditingController _controller = TextEditingController();
  List<FoodItem> _searchResults = [];
  bool _searched = false;
  bool _loading = false;
  String? _error;

  Future<void> _onSearchPressed() async {
    final keyword = _controller.text.trim();
    if (keyword.isEmpty) return;

    setState(() {
      _loading = true;
      _error = null;
      _searched = true;
      _searchResults = [];
    });

    try {
      final results = await SearchRecipeService.search(keyword);
      setState(() {
        _searchResults = results;
      });
    } catch (e) {
      setState(() {
        _error = '에러 발생: $e';
       });
    } finally {
      setState(() {
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final keyword = _controller.text.trim();

    return GradientBackground(
      child: Scaffold(
        backgroundColor: Colors.transparent,
        body: SafeArea(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 10, 16, 20),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 25, vertical: 25),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(20),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Center(
                    child: Text(
                      '레시피 검색',
                      style: TextStyle(
                        fontSize: 30,
                        fontWeight: FontWeight.bold,
                        color: Color.fromARGB(255, 66, 105, 50),
                      ),
                    ),
                  ),
                  SizedBox(height: 20),
                  // 검색창
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12),
                    decoration: BoxDecoration(
                      color: Colors.grey[200],
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Row(
                      children: [
                        const Icon(Icons.search, color: Colors.black54),
                        const SizedBox(width: 8),
                        Expanded(
                          child: TextField(
                            controller: _controller,
                            textInputAction: TextInputAction.search,
                            onSubmitted: (_) => _onSearchPressed(),
                            decoration: const InputDecoration(
                              hintText: '원하는 음식을 검색해보세요!',
                              border: InputBorder.none,
                            ),
                          ),
                        ),
                        ElevatedButton(
                          onPressed: _onSearchPressed,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color.fromARGB(
                              255,
                              235,
                              239,
                              165,
                            ),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(8),
                            ),
                          ),
                          child: const Text(
                            '검색',
                            style: TextStyle(color: Colors.black),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 20),

                  Expanded(
                    child: !_searched
                        ? const SizedBox() 
                        : _loading
                          ? const Center(child: CircularProgressIndicator())
                          : _error != null 
                            ? Center(child: Text(_error!))
                            : _searchResults.isEmpty
                              ? const Center(child: Text('검색 결과가 없습니다.'))
                              : SearchResultsView(
                                results: _searchResults,
                                keyword: keyword,
                              ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
