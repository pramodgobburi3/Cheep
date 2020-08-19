from django.db import models
from django.contrib.auth.models import User

# Create your models here.
class Budgets(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    name = models.CharField(max_length=255)
    limit = models.DecimalField(decimal_places=2, max_digits=11)
    spent = models.DecimalField(decimal_places=2, max_digits=11)
    color = models.CharField(max_length=100)
    last_transaction = models.CharField(max_length=10, blank=True, null=True)
    category_id = models.IntegerField(null=True, blank=False)


    class Meta:
        managed = True
        db_table = "budgets"