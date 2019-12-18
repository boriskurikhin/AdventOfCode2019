req = {}
with open('input_file.txt', 'r') as file:
    for line in file:
        if not line.strip(): break
        inp = line.split('=>')
        resn, resq = inp[1].strip().split(' ')[1], int(inp[1].strip().split(' ')[0])
        req[resn] = {'q' : resq, 'r' : {}}
        for r in inp[0].strip().split(','):
            r = r.strip().split(' ')
            req[resn]['r'][r[1]] = int(r[0])
need = {'FUEL': 1}
extra = {}
while True:
    needkeys = need.keys()
    reacted = False
    for item in needkeys:
        if item not in need or item == 'ORE': 
            continue
        qtyneed = need[item]
        if qtyneed <= 0:
            del need[item]
            continue
        if item in extra and extra[item] >= qtyneed:
            need[item] = 0
            extra[item] -= qtyneed
            continue
        #make one q unit at a time? no multiplying?
        q = req[item]['q']
        reacted = True
        need[item] -= q #use it
        if need[item] < 0:
            extra[item] = extra.get(item, 0) + abs(need[item])
        for reqs in req[item]['r']:
            need[reqs] = need.get(reqs, 0) + req[item]['r'][reqs]
        break
    if not reacted: 
        break
print(need['ORE'])
