from django.urls import path
from news.views import detail_news_view

app_name = 'news'

urlpatterns = [
    path('<slug>/', detail_news_view, name="detail"),
 ]