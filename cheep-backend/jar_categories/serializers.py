from rest_framework import serializers
from .models import JarCategories

class JarCategorySerializer(serializers.ModelSerializer):

    class Meta:
        model = JarCategories
        fields = "__all__"
