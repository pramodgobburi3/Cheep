from rest_framework import serializers
from .models import Accounts


class AccountSerializer(serializers.ModelSerializer):

    class Meta:
        model = Accounts
        fields = ("accessToken", "accountId", "name")


class AccountIdSerializer(serializers.ModelSerializer):

    class Meta:
        model = Accounts
        fields = ["accountId"]