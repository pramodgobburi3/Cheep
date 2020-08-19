from django.db import models
from accounts.models import Accounts
from categories.models import Category

# Create your models here.
class Transactions(models.Model):
    account = models.ForeignKey(Accounts, on_delete=models.CASCADE, null=False, blank=False)
    plaid_transaction_id = models.CharField(max_length=250, null=True, blank=False)
    transaction_category = models.CharField(max_length=250)
    transaction_name = models.CharField(max_length=100)
    transaction_datetime = models.DateField()
    transaction_amount = models.DecimalField(max_digits=11, decimal_places=2)

    class Meta:
        managed=True,
        db_table = "transactions"