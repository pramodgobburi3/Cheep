from rest_framework import serializers
from .models import Jars
from jar_categories.serializers import *

class JarsSerializer(serializers.ModelSerializer):
    jar_categories = JarCategorySerializer(many=True, read_only=True)

    class Meta:
        model = Jars
        fields = ['id', 'user', 'jar_name', 'jar_amount', 'jar_fraction', 'start_date', 'jar_categories']