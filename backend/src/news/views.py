from django.shortcuts import render, get_object_or_404
from django.db.models import Q

from news.models import NewsPost

def detail_news_view(request, slug):
	
	context = {}

	news_post = get_object_or_404(NewsPost, slug=slug)
	context['news_post'] = news_post

	return render(request, 'news/detail_news.html', context)

def get_news_queryset(query=None):
	queryset = []
	queries = query.split(" ")
	for q in queries:
		posts = NewsPost.objects.filter(
			Q(title__contains=q)|
			Q(body__icontains=q)
			).distinct()
		for post in posts:
			queryset.append(post)

	return list(set(queryset))