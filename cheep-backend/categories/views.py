from django.shortcuts import render
from django.http import JsonResponse
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt
import json
from .models import *

# Create your views here.
@csrf_exempt
def populate_categories(request):
    if request.method == 'POST':
        body = json.loads(request.body)
        categories = body['categories']

        for category in categories:
            category_id = category['category_id']
            plaid_category_name = category['plaid_category_name']
            category_name = category['category_name']
            c = Category.objects.create(category_id=category_id, plaid_category_name=plaid_category_name, category_name=category_name)

        return JsonResponse({"status": "successful"}, safe=False)