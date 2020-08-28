from rest_framework import serializers

from news.models import NewsPost


class NewsPostSerializer(serializers.ModelSerializer):

	class Meta:
		model = NewsPost
		fields = ['pk', 'title', 'slug', 'body', 'image', 'date_updated']