from django.urls import path

from news.api.views import (

	api_detail_news_view,
	ApiNewsListView,

	)

app_name = 'news'

urlpatterns = [
	path('<slug>/', api_detail_news_view, name="detail"),
	path('list', ApiNewsListView.as_view(), name="list"),
]