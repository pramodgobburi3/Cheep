from rest_framework import serializers
from users.serializers import UserProfileSerializer
from .models import PlaidProfile


class PlaidProfileSerializer(serializers.ModelSerializer):
    user = UserProfileSerializer()

    class Meta:
        model = PlaidProfile
        fields = "__all__"


class PlaidAccessTokenSerializer(serializers.ModelSerializer):

    class Meta:
        model= PlaidProfile
        fields = ["access_token"]
