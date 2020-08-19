from django.db import models
from users.models import UserProfile

# Create your models here.
class PlaidProfile(models.Model):
    access_token = models.CharField(max_length=255, blank=False, null=False)
    user = models.ForeignKey(UserProfile, on_delete=models.CASCADE)

    class Meta:
        managed = True
        db_table = "plaid_profile"

