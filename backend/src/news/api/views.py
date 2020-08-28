from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view
from rest_framework.pagination import PageNumberPagination
from rest_framework.generics import ListAPIView

from news.models import NewsPost
from news.api.serializers import NewsPostSerializer

@api_view(['GET', ])
def api_detail_news_view(request, slug):
	try:
		news_post = NewsPost.objects.get(slug=slug)
	except NewsPost.DoesNotExist:
		return Response(status=status.HTTP_404_NOT_FOUND)

	if request.method == 'GET':
		serializer = NewsPostSerializer(news_post)
		return Response(serializer.data)


class ApiNewsListView(ListAPIView):
	queryset = NewsPost.objects.all()
	serializer_class = NewsPostSerializer
	pagination_class = PageNumberPagination