# Create your models here.
from django.db import models
from django.contrib.auth.models import User


# Create your models here.
class Accounts(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    accessToken = models.CharField(max_length=255, null=True, blank=False)
    accountId = models.CharField(max_length=255, null=False, blank=False)
    name = models.CharField(max_length=255)


    class Meta:
        managed = True
        db_table = "accounts"