from django.shortcuts import render
from operator import attrgetter
from django.core.paginator import EmptyPage, PageNotAnInteger, Paginator

from news.views import get_news_queryset
from news.models import NewsPost


NEWS_POSTS_PER_PAGE = 10

def home_screen_view(request): 

	context = {}

	query = ""
	query = request.GET.get('q', '')
	context['query'] = str(query)
	print("home_screen_view: " + str(query))

	news_posts = sorted(get_news_queryset(query), key=attrgetter('date_updated'), reverse=True)
	
	# Pagination
	page = request.GET.get('page', 1)
	news_posts_paginator = Paginator(news_posts, NEWS_POSTS_PER_PAGE)

	try:
		news_posts = news_posts_paginator.page(page)
	except PageNotAnInteger:
		news_posts = news_posts_paginator.page(NEWS_POSTS_PER_PAGE)
	except EmptyPage:
		news_posts = news_posts_paginator.page(news_posts_paginator.num_pages)

	context['news_posts'] = news_posts

	return render(request, "personal/home.html", context)